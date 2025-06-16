pipeline {
    
    agent any
        
    tools {
        maven 'Maven-3.9.9'
        
    }

    stages {
        stage ('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/kbabah/primuslearningapp.git'
            }
        }
    }

        stage('Build') {
            steps {
                sh "mvn clean compile"
        }
    }

        stage("build & SonarQube analysis") {
            steps {
                withSonarQubeEnv('sonaqube') {
                sh 'mvn sonar:sonar'
              }
            }
          }

 }
}
