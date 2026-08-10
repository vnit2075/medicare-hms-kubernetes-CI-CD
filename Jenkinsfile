pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'vnit2075'
        IMAGE_NAME      = 'medicare-hms'
        IMAGE_TAG       = "${BUILD_NUMBER}"
        K8S_NAMESPACE   = 'medicare'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/vnit2075/medicare-hms-kubernetes-CI-CD.git'
            }
        }

        stage('2. Build Maven Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3. Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh "docker login -u ${USER} -p ${PASS}"
                        sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
                        sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest ."
                        sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                        sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('4. Deploy to Kubernetes Cluster') {
            steps {
                script {
                    sh """
                        kubectl apply -f k8s/namespace.yaml
                        kubectl apply -f k8s/mysql-secret.yaml
                        kubectl apply -f k8s/mysql-pv-pvc.yaml
                        kubectl apply -f k8s/mysql-deployment.yaml
                        kubectl apply -f k8s/mysql-service.yaml
                        kubectl apply -f k8s/app-deployment.yaml
                        kubectl apply -f k8s/app-service.yaml
                        kubectl set image deployment/medicare-app medicare-app=${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG} -n ${K8S_NAMESPACE} || true
                        kubectl rollout status deployment/medicare-app -n ${K8S_NAMESPACE}
                    """
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
        success {
            echo 'CI/CD Pipeline Completed Successfully!'
        }
    }
}
