pipeline {
    
    agent any
        
    tools {
        maven 'Maven-3.9.9'
        
    }

    stages {
        stage('Git Pull') {
            steps {
      // One or more steps need to be included within the steps block.
         git 'https://github.com/kbabah/primuslearningapp.git'
        }
    }

  stage('Build') {
    steps {
        sh "mvn clean compile"
      // One or more steps need to be included within the steps block.
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
