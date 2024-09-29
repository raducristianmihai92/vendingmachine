pipeline {
	agent {
        docker {
            image 'maven:3.8.1-jdk-11' // Specify the Maven Docker image with Java
            args '-v $HOME/.m2:/root/.m2' // Optional: Mount the .m2 directory for caching dependencies
        }
    }
    stages {
        stage('Checkout') {
            steps {
                // Checkout the repository
                git 'https://github.com/raducristianmihai92/vendingmachine'
            }
        }
        stage('Build') {
            steps {
                // Build the application using Maven
                sh 'mvn clean package'
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    // Build the Docker image
                    docker.build('vendingmachine-app')
                }
            }
        }
        stage('Docker Push') {
            steps {
                script {
                    // Push the Docker image to Docker Hub
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-credentials') {
                        docker.image('vendingmachine-app').push()
                    }
                }
            }
        }
        stage('Run App') {
            steps {
                script {
                    // Stop any running containers with the same name (if any) and start the new one
                    sh '''
                    docker stop vendingmachine-app || true
                    docker rm vendingmachine-app || true
                    docker run -d --name vendingmachine-app -p 8080:8080 vendingmachine-app
                    '''
                }
            }
        }
    }
}
