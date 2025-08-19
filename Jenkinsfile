pipeline { 
    agent any
        
    tools {
        maven 'Maven-3.9.9'
        dockerTool 'docker'
    }

    environment {
        APPNAME = "babahdev/primuslearningapp" 
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
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
                sh 'docker build -t $APP_NAME:$BUILD_NUMBER .' 
            }
        }
        stage('login to dockerhub') {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        
         stage('push image') {
            steps {
                sh 'docker push $APP_NAME:$BUILD_NUMBER'
            }
        }
        
      }
    }

    
