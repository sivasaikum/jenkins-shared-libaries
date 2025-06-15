def call (Map configMap) {
    pipeline {
    agent { label 'STAGE-1'}
    environment {
        PROJECT = configMap.get('project')
        COMPONENT = configMap.get('component')
        appVersion = ''
        ACC_ID = '381491879282'
    }
    parameters {
        booleanParam(name: 'deploy', defaultValue: false, description: 'select when you are ready to deploy')
    }    

    stages {
        stage('Read Json Version') {
            steps {
                script {
                    def JsonRead = readJSON file: 'package.json'
                    appVersion = JsonRead.version
                    echo " Version is $appVersion"
                }
            }
        }

        stage ('Install Dependencies') {
            steps {
                script {
                    sh """
                        npm install
                    """
                }
            }
        }

        stage ('Docker Build') {
            steps {
                script {
                    withAWS(region: 'us-east-1', credentials: 'aws-cred') {
                        sh """
                            aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com
                            docker build -t ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com/${PROJECT}/${COMPONENT}:${appVersion} .
                            docker push ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com/${PROJECT}/${COMPONENT}:${appVersion}
                            echo "docker image is successfuly puhed to AWS ECR"
                        """
                    }
                    
                }
            }
        }

        stage('Trigger Deploy') {
            when {
                expression {
                    params.deploy
                }
            }
            steps {
                build job: 'backend-cd', parameters: [string(name:'version', value: "${appVersion}")], propagate: true

            }
        }
    }
}
}