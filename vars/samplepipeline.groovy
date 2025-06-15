def call (Map configMap) {
    pipeline {
        agent { label 'STAGE-1'}
        environment {
            greet = configMap.get('greet')
        }    

        stages {
            stage('Greet') {
                steps {
                    script {
                        sh """
                        echo " Version is $greet"
                        """
                    }
                }
            }
        }
    }   
}