stage('pull source code') {
    node('automation'){
        git([url: 'https://github.com/Nick0136/iWeb.git', branch: 'master'])
    }
}

stage('maven compile & package') {
    node('automation'){
        sh ". /etc/profile"
        sh ". ~/.bash_profile"

        //定义maven java环境
       // def mvnHome = tool 'M36'
        def dkHome = tool 'docker_node'
        env.PATH = "${dkHome}/bin:${env.PATH}"
      //  env.PATH = "${jdkHome}/bin:${env.PATH}"
        sh "mvn clean install"
        sh "mv target/iWeb.war target/ROOT.war"
    }
}

stage('clean docker environment') {
    node('automation'){
        try{
            sh 'docker stop iWebObj'
        }catch(exc){
            echo 'iWebObj container is not running!'
        }

        try{
            sh 'docker rm iWebObj'
        }catch(exc){
            echo 'iWebObj container does not exist!'
        }
        try{
            sh 'docker rmi iweb'
        }catch(exc){
            echo 'iweb image does not exist!'
        }
    }
}

stage('make new docker image') {
    node('automation'){
        try{
            sh 'docker build -t iweb .'
        }catch(exc){
            echo 'Make iweb docker image failed, please check the environment!'
        }
    }
}

stage('start docker container') {
    node('automation'){
        try{
            sh 'docker run --name iWebObj -d -p 8111:8080 iweb'
        }catch(exc){
            echo 'Start docker image failed, please check the environment!'
        }
    }
}
