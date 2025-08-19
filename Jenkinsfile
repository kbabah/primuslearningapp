pipeline { 
    agent any
        
    tools {
        maven 'Maven-3.9.9'
    }

    environment {
        registry = "babahdev/primuslearningapp" 
        registryCredential = 'dockerhub' 
        dockerImage = '' 
        // Add JVM arguments to fix Java module access issues for SonarQube
        MAVEN_OPTS = '--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED'
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/kbabah/primuslearningapp.git'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            
        }

        stage('Build') {
            steps {
                sh 'mvn package'
            }
        }

        // stage('Package') {
        //     steps {
        //         sh 'mvn package -DskipTests'
        //     }
        //     post {
        //         success {
        //             archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        //         }
        //     }
        // 

        // stage('SonarQube Analysis') {
        //     steps {
        //         withSonarQubeEnv('sonaqube') {
        //             sh '''
        //                 mvn sonar:sonar \
        //                 -Dsonar.projectKey=web-app \
        //                 -Dsonar.java.jvmArgs="--add-opens java.base/java.lang=ALL-UNNAMED"
        //             '''
        //         }
        //     }
        // }

        stage('Docker Image Build')  {
            steps {
                script {
                dockerImage = docker.build registry + ":$BUILD_NUMBER" 
                }
            }
        }
        stage('Deploy our image') { 
            steps { 
                script { 
                    docker.withRegistry( '', registryCredential ) { 
                        dockerImage.push() 
                    }
               } 
            }
        } 
        stage('Cleaning up') { 
            steps { 
                sh "docker rmi $registry:$BUILD_NUMBER" 
            }
        } 
        
      }
    }
}

    
