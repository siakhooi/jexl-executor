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

docker-shellcheck:
	docker run --rm -v $$(pwd):/workspace docker.io/siakhooi/devcontainer:bash-deb-0.1.0 scripts/shellcheck.sh
docker-build-deb:
	docker run --rm -v $$(pwd):/workspace docker.io/siakhooi/devcontainer:bash-deb-0.1.0 scripts/build-deb.sh
docker-build-rpm:
	docker run --rm -v $$(pwd):/workspace docker.io/siakhooi/devcontainer:bash-rpm-0.1.0 scripts/build-rpms.sh
