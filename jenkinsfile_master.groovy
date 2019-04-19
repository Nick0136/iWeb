stage('pull source code') {
    node('master'){
        //git([url: 'git@github.com:Nick0136/iWeb.git', branch: 'master'])
        
        git([url: 'https://github.com/Nick0136/iWeb.git', branch: 'master'])
    }
}

stage('maven compile & package') {
    node('master'){
        shell ". /etc/profile"
        shell ". ~/.bash_profile"

        //定义maven java环境
        //def mvnHome = tool 'M36'
        //def jdkHome = tool 'jdk1.8_master'
       // def dkHome = tool 'docker_master'
        //env.PATH = "${mvnHome}/bin:${env.PATH}"
        //env.PATH = "${jdkHome}/bin:${env.PATH}"
       // env.PATH = "${dkHome}/bin:${env.PATH}"
        shell "mvn clean install"
        shell "mv target/iWeb.war target/ROOT.war"
    }
}

stage('clean docker environment') {
    node('master'){
        try{
          //  sh 'service docker start'
            shell 'docker stop iWebObj'
        }catch(exc){
            echo 'iWebObj container is not running!'
        }

        try{
            shell 'docker rm iWebObj'
        }catch(exc){
            echo 'iWebObj container does not exist!'
        }
        try{
            shell 'docker rmi iweb'
        }catch(exc){
            echo 'iweb image does not exist!'
        }
    }
}

stage('make new docker image') {
    node('master'){
        try{
            shell 'docker build -t iweb .'
        }catch(exc){
            echo 'Make iweb docker image failed, please check the environment!'
        }
    }
}

stage('start docker container') {
    node('master'){
        try{
            shell 'docker run --name iWebObj -d -p 8111:8080 iweb --privileged'
        }catch(exc){
            echo 'Start docker image failed, please check the environment!'
        }
    }
}
