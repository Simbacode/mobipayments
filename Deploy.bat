ssh-agent

ssh-add C:\Users\User\.ssh\id_rsa.pub

mvn release:clean release:prepare release:perform -B -e | tee maven-central-deploy.log

ssh-add -D