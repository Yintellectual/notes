Guide to installing 3rd party JARs

https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
	
	#packaging can be jar or pom, all parameters can be found in POM.xml
	mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
	-DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
	
where -Dpackageing=jar or war

Providing port in command line

	mvn spring-boot:run -Drun.jvmArguments='-Dserver.port=8081'
	
	
	
1. System scope example:

		<dependency>
			<groupId>com.taobao</groupId>
			<artifactId>taobao-sdk-java</artifactId>
			<version>1.0</version>
			<systemPath>C:/zhiyuninfo/serviceportal/taobao-sdk-java-1.0.jar</systemPath>
			<scope>system</scope>
		</dependency>

	1.1 system scoped jars do not package into jar by default. To solve this issue, add the jar into local repository.	
		
		
2. Multi module

	2.1 multi module in mvn is simple.
	
	2.2 in parent 
	
		2.2.1 add <modules>
		
		2.2.2 set <packaging> to be pom
	
		2.2.3 note that the parent can not be both parent and make war
		
	2.3 in module,
	
		2.3 set <parent> to be the parent
		
		2.4 note that version must match