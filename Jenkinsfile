def COLOR_MAP = [
    'SUCCESS': 'good', 
    'FAILURE': 'danger',
    'UNSTABLE': 'danger'
]
pipeline {
    agent any
    environment {
        SCANNER_HOME=tool 'SonarScanner'
        SNYK_HOME   = tool name: 'Snyk'
    }
    tools {
        snyk 'Snyk'
    }
     
    stages {
        // Connect to GitHub Repo
        stage('Checkout Source'){
            steps {
                git branch: 'main',
                credentialsId: 'GitHub-Credential',
                url: 'https://github.com/dampad0/kube-springreetings.git'
            }
        } 
        // SonarQube SAST Code Analysis
        //stage("SonarQube SAST Analysis"){
        //    steps{
        //        withSonarQubeEnv('Sonar-Server') {
        //            sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=springreeting-app-service \
        //            -Dsonar.projectKey=springreeting-app-service '''
        //        }
        //        
        //    }
        //}
        // Providing Snyk Access
        stage('Authenticate & Authorize Snyk') {
            steps {
                withCredentials([string(credentialsId: 'Snyk-API-Token', variable: 'SNYK_TOKEN')]) {
                    sh "${SNYK_HOME}/snyk-linux auth $SNYK_TOKEN"
                }
            }
        }
        // Scan Service Dockerfile With Open Policy Agent (OPA)
        stage('OPA Dockerfile Vulnerability Scan') {
            steps {
                sh "docker run --rm -v ${WORKSPACE}:/project openpolicyagent/conftest test --policy docker-opa-security.rego Dockerfile || true"
            }
        }
        // Build and Tag Service Docker Image
        stage('Build & Tag Microservice Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'DockerHub-Credential', toolName: 'docker') {
                        sh "docker build -t dampado/springreetings:latest ."
                    }
                }
            }
        }
        // Execute SCA/Dependency Test on Service Docker Image
        stage('Snyk SCA Test | Dependencies') {
            steps {
                sh "${SNYK_HOME}/snyk-linux test --docker dampado/springreetings:latest || true" 
            }
        }
        // Push Service Image to DockerHub
        stage('Push Microservice Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'DockerHub-Credential', toolName: 'docker') {
                        sh "docker push dampado/springreetings:latest "
                    }
                }
            }
        }
        stage('Deploy Microservice To The Stage/Test Env'){
            steps{
                script{
                    withKubeConfig([credentialsId: 'Kubernetes-Credential', clusterName: 'EKS_Cluster', namespace: 'test-env', serverUrl: 'https://4552F0D3B039B074E11004E92F7F0F0D.gr7.us-east-2.eks.amazonaws.com']) {
                       sh 'kubectl apply -f deploy-envs/test-env/deployment.yaml -n test-env -sa group-app-service-account'
                       sh 'kubectl apply -f deploy-envs/test-env/nodeport-service.yaml -n test-env -sa group-app-service-account'  //NodePort Service
                       //sh 'kubectl apply -f deploy-envs/test-namespace.yaml' 
                   }
                }
            }
        }
        // // Perform DAST Test on Application
        // stage('ZAP Dynamic Testing | DAST') {
        //     steps {
        //         sshagent(['OWASP-Zap-Credential']) {
        //             sh 'ssh -o StrictHostKeyChecking=no ubuntu@18.224.21.15 "docker run -t zaproxy/zap-weekly zap-baseline.py -t http://3.144.169.138:30000/" || true'
        //                                                 //JENKINS_PUBLIC_IP                                                  //EKS_WORKER_NODE_IP_ADDRESS:30000
        //         }
        //     }
        // }
        // // Production Deployment Approval
        // stage('Approve Prod Deployment') {
        //     steps {
        //             input('Do you want to proceed?')
        //     }
        // }
        // // // Deploy to The Production Environment
        // stage('Deploy Microservice To The Prod Env'){
        //     steps{
        //         script{
        //             withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'Kubernetes-Credential', namespace: '', restrictKubeConfigAccess: false, serverUrl: '') {
        //                sh 'kubectl apply -f deploy-envs/prod-env/deployment.yaml'
        //                sh 'kubectl apply -f deploy-envs/prod-env/loadbalancer-service.yaml'  //LoadBalancer Service
        //                //sh 'kubectl apply -f deploy-envs/prod-namespace.yaml'  
        //             }
        //         }
        //     }
        // }
    }
    post {
    always {
        echo 'Slack Notifications.'
        slackSend channel: 'dbsg-multi-services-alert', //update and provide your channel name
        color: COLOR_MAP[currentBuild.currentResult],
        message: "*${currentBuild.currentResult}:* Job Name '${env.JOB_NAME}' build ${env.BUILD_NUMBER} \n Build Timestamp: ${env.BUILD_TIMESTAMP} \n Project Workspace: ${env.WORKSPACE} \n More info at: ${env.BUILD_URL}"
    }
  }
}