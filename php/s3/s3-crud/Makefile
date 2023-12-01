PHP = $(shell command -v php 2> /dev/null)
COMPOSER = $(shell command -v composer 2> /dev/null)

install:
	@if [ -z "$(PHP)" ]; then \
		echo "php is not available, please install php"; \
		exit 1; \
	fi
	@if [ -z "$(COMPOSER)" ]; then \
		echo "composer is not available, please install composer"; \
		echo "installation instructions: https://getcomposer.org/download/"; \
		exit 1; \
	fi
	@composer init
	@composer require monolog/monolog
	@composer require aws/aws-sdk-php

run:
	@php s3-basic.php