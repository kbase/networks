#
#Makefile for Build and setup MogoDBService
#make sure that these two variables was set:
#	JAVA_HOME
#	GLASSFISH_HOME

#Apache ANT compiler
ANT=ant

all:
	cd ./conf; $(ANT) build 

deploy: stop_domain1 start_domain1 deploy_war generate_script
	
stop_domain1:
	$(GLASSFISH_HOME)/bin/asadmin stop-domain 

start_domain1:
	@START_RESULT='$(shell $(GLASSFISH_HOME)/bin/asadmin start-domain | grep successfully)'
	@if ["$(START_RESULT)" != ""]; then exit; fi
		
deploy_config:
	cp domain.xml $(GLASSFISH_HOME)/glassfish/domains/domain1/config/
	@VERIFY_RESULT='$(shell $(GLASSFISH_HOME)/bin/asadmin verify-domain-xml domain1 | grep valid)'
	@if ["" == "$(VERIFY_RESULT)"]; then exit; fi 

deploy_war:
	@DEPLOY='$(shell $(GLASSFISH_HOME)/bin/asadmin deploy ./dist/KBaseNetworksRPC.war | grep failed)'
	@if ["$(DEPLOY)" != ""];\
	then\
		$(GLASSFISH_HOME)/bin/asadmin undeploy KBaseNetworksRPC;\
		$(GLASSFISH_HOME)/bin/asadmin deploy ./dist/KBaseNetworksRPC.war;\
        fi\

generate_script:
	@echo 'sudo '$(GLASSFISH_HOME)'/bin/asadmin start-domain ' > start_service.sh
	chmod 755 start_service.sh
	@echo 'sudo '$(GLASSFISH_HOME)'/bin/asadmin stop-domain ' > stop_service.sh
	chmod 755 stop_service.sh

clean:
	cd ./conf; $(ANT) clean	
