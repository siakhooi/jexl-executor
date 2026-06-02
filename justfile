all-deb: clean set-version build build-deb
all-rpm: clean set-version build build-rpms
jar: clean set-version build

clean:
	mvn clean
	rm -f siakhooi-jexl-executor_*_amd64.deb \
		siakhooi-jexl-executor_*_amd64.deb.sha256sum \
		siakhooi-jexl-executor_*_amd64.deb.sha512sum \
		siakhooi-jexl-executor-*.rpm \
		siakhooi-jexl-executor-*.rpm.sha256sum \
		siakhooi-jexl-executor-*.rpm.sha512sum \

set-version:
	scripts/set-version.sh
release:
	scripts/create-release.sh

build:
	mvn clean verify
build-deb:
	scripts/build-deb.sh
build-rpms:
	scripts/build-rpms.sh
run-help:
	java -jar target/jexl-executor.jar --help
run:
	java -jar target/jexl-executor.jar \
		examples/context.json \
		examples/script1.jexl \
		examples/script2.jexl \
		examples/script2a.json \
		examples/script3.jexl
run2:
	java -jar target/jexl-executor.jar \
		-e 'script2.total > 1000? 0: 1' \
		examples/context.json \
		examples/script2.jexl
run2e:
	java -jar target/jexl-executor.jar \
		-e '@file:examples/exit.jexl' \
		examples/context.json \
		examples/script2.jexl
runf1:
	cd examples && \
	java -jar ../target/jexl-executor.jar \
		-c execution-config.yaml
runf2:
	cd examples && \
	java -jar ../target/jexl-executor.jar \
		-c execution-config.yaml --id flow3
runf3:
	cd examples && \
	java -jar ../target/jexl-executor.jar \
		--config --id flow3
run-jexl-debug:
	java -jar target/jexl-executor.jar --jexl-debug \
		examples/context.json \
		examples/script1.jexl \
		examples/script2.jexl \
		examples/script2a.json \
		examples/script3.jexl
run-debug:
	java -jar target/jexl-executor.jar --debug \
		examples/context.json \
		examples/script1.jexl \
		examples/script2.jexl \
		examples/script2a.json \
		examples/script3.jexl

qlty-check:
	qlty check --all

root := justfile_directory()
docker-shellcheck:
	docker run --rm -v {{ root }}:/workspaces docker.io/siakhooi/devcontainer:deb2604 scripts/shellcheck.sh
docker-build-deb:
	docker run --rm -v {{ root }}:/workspaces docker.io/siakhooi/devcontainer:deb2604 scripts/build-deb.sh
docker-build-rpm:
	docker run --rm -v {{ root }}:/workspaces docker.io/siakhooi/devcontainer:rpm44 scripts/build-rpms.sh
build-packages: docker-build-deb docker-build-rpm
