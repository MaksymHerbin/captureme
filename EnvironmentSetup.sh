echo "Setting up maven 3.5.*"
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

echo 'Setting up java 8'
sudo yum install java-1.8.0-openjdk-devel -y

echo 'Make java 8 default version'
sudo update-alternatives --config java
sudo update-alternatives --config javac
