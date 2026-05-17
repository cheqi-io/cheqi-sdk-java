ENV ?= test

SPEC_LOCAL_URL := http://localhost:8080/v3/api-docs.yaml
SPEC_TEST_URL := https://test.api.cheqi.io/v3/api-docs.yaml
SPEC_SANDBOX_URL := https://sandbox.api.cheqi.io/v3/api-docs.yaml
SPEC_PROD_URL := https://api.cheqi.io/v3/api-docs.yaml

ifeq ($(ENV),local)
SPEC_URL := $(SPEC_LOCAL_URL)
else ifeq ($(ENV),test)
SPEC_URL := $(SPEC_TEST_URL)
else ifeq ($(ENV),sandbox)
SPEC_URL := $(SPEC_SANDBOX_URL)
else ifeq ($(ENV),prod)
SPEC_URL := $(SPEC_PROD_URL)
else
$(error Unsupported ENV '$(ENV)'. Use one of: local, test, sandbox, prod)
endif
GENERATED_DIR := src/main/java/com/cheqi/sdk/models/generated

fetch-spec:
	curl -fsSL $(SPEC_URL) -o openapi.yaml
	@echo "Spec updated. Run 'make generate' to regenerate models."

fetch-spec-local:
	$(MAKE) fetch-spec ENV=local

generate:
	rm -rf $(GENERATED_DIR)
	mvn generate-sources -pl .
	@echo "Models generated in $(GENERATED_DIR)/"

spec-check:
	@curl -fsSL $(SPEC_URL) -o openapi.yaml.tmp
	@if diff -q openapi.yaml openapi.yaml.tmp > /dev/null 2>&1; then \
		echo "Spec is up to date."; \
	else \
		echo "ERROR: Local openapi.yaml differs from $(SPEC_URL)"; \
		echo "Run 'make fetch-spec && make generate' to update."; \
		diff openapi.yaml openapi.yaml.tmp || true; \
		rm openapi.yaml.tmp; \
		exit 1; \
	fi
	@rm -f openapi.yaml.tmp

.PHONY: fetch-spec fetch-spec-local generate spec-check
