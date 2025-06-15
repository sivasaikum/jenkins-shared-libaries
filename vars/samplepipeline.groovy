def call (Map configMap) {
    pipeline {
        agent { label 'STAGE-1'}
        environment {
            greet = configMap.get('greet')
        }
        parameters {
            booleanParam(name: 'deploy', defaultValue: false, description: 'select when you are ready to deploy')
        }    

        stages {
            stage('Greet') {
                steps {
                    script {
                        echo " Version is $greet"
                    }
                }
            }
        }
    }   
}