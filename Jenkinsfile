pipeline {
    agent any

    tools {
        // JDK configurado en Jenkins (Manage Jenkins → Tools)
        jdk 'jdk-17'

        // Maven configurado en Jenkins (Manage Jenkins → Tools)
        maven 'maven-3.9.11'
    }

    options {
        // Agrega timestamps a los logs
        timestamps()

        // Evita ejecuciones concurrentes del mismo job
        disableConcurrentBuilds()
    }

    parameters {
        choice(
            name: 'GRID_TARGET',
            choices: ['local', 'aws'],
            description: 'Where Selenium Grid is running'
        )
    }

    environment {

        // ================================
        // Selenium Grid URLs
        // ================================
        AWS_GRID_URL   = 'http://selenium-hub:4444/wd/hub'
        LOCAL_GRID_URL = 'http://host.docker.internal:4444/wd/hub'

        // ================================
        // Allure results directory (ESTÁNDAR)
        // ================================
        ALLURE_RESULTS = 'target/allure-results'

        // ================================
        // Maven flags comunes
        // ================================
        MAVEN_FLAGS = '-Dheadless=true -DCI=true'

        // ================================
        // S3 configuration (enterprise)
        // ================================
        S3_BUCKET = 'your-s3-bucket-name'
        S3_PATH   = 'selenium-web/allure-results'
    }

    stages {

        stage('Clean workspace & Allure data') {
            steps {

                // Limpieza TOTAL para evitar reportes antiguos
                sh '''
                    echo "Cleaning workspace and Allure artifacts"

                    rm -rf target/allure-results
                    rm -rf allure-report
                '''
            }
        }

        stage('Run tests') {
            steps {
                script {

                    // Selección dinámica del Grid
                    def gridUrl = (params.GRID_TARGET == 'aws')
                        ? env.AWS_GRID_URL
                        : env.LOCAL_GRID_URL

                    echo "GRID_TARGET = ${params.GRID_TARGET}"
                    echo "GRID_URL    = ${gridUrl}"

                    // =================================
                    // LOCAL → solo Chrome (secuencial)
                    // =================================
                    if (params.GRID_TARGET == 'local') {

                        sh """
                            mvn clean test ${env.MAVEN_FLAGS} \
                              -Dbrowser=chrome \
                              -DseleniumGridUrl=${gridUrl}
                        """
                    }

                    // =================================
                    // AWS → Chrome + Firefox (paralelo)
                    // =================================
                    if (params.GRID_TARGET == 'aws') {

                        parallel(

                            Chrome: {
                                echo 'Running tests on CHROME'

                                sh """
                                    mvn test ${env.MAVEN_FLAGS} \
                                      -Dbrowser=chrome \
                                      -DseleniumGridUrl=${gridUrl}
                                """
                            },

                            Firefox: {
                                echo 'Running tests on FIREFOX'

                                sh """
                                    mvn test ${env.MAVEN_FLAGS} \
                                      -Dbrowser=firefox \
                                      -DseleniumGridUrl=${gridUrl}
                                """
                            }
                        )
                    }
                }
            }
        }

        stage('Upload Allure results to S3') {

            when {
                // SOLO en AWS
                expression { params.GRID_TARGET == 'aws' }
            }

            steps {

                echo 'Uploading Allure results to S3'

                // Uso de credenciales AWS desde Jenkins
                withAWS(credentials: 'aws-credentials-id', region: 'us-east-1') {

                    sh '''
                        aws s3 sync target/allure-results \
                          s3://$S3_BUCKET/$S3_PATH/$BUILD_NUMBER/
                    '''
                }
            }
        }
    }

    post {

        always {

            echo 'Publishing Allure report'

            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: "${ALLURE_RESULTS}"]]
            )
        }
    }
}
