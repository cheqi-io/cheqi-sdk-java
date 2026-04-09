SPEC_URL ?= https://test.api.cheqi.io/v3/api-docs.yaml
LOCAL_SPEC_URL ?= http://localhost:8080/v3/api-docs.yaml

fetch-spec:
	curl -fsSL $(SPEC_URL) -o openapi.yaml
	@echo "Spec updated. Run 'make generate' to regenerate models."

fetch-spec-local:
	curl -fsSL $(LOCAL_SPEC_URL) -o openapi.yaml
	@echo "Local spec updated from $(LOCAL_SPEC_URL). Run 'make generate' to regenerate models."

generate:
	mvn generate-sources -pl .
	@echo "Models generated in src/main/java/com/cheqi/sdk/models/generated/"

.PHONY: fetch-spec fetch-spec-local generate
