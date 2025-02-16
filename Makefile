.PHONY: run report install

report:
	mvn clean surefire-report:report

install:
	mvn clean install
