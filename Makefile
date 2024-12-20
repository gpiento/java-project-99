.DEFAULT_GOAL := build-run
.PHONY: build app

setup:
	./gradlew wrapper --gradle-version 8.10.2

clean:
	./gradlew clean

build:
	./gradlew build

install:
	./gradlew install

run-dist: install
	@./build/install/app/bin/app

run: build
	@java -jar ./app/build/libs/app-0.0.1-SNAPSHOT.jar

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

check-deps:
	./gradlew dependencyUpdates -Drevision=release

build-run: build install run-dist

