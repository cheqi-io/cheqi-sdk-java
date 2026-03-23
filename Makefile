SPEC_URL := https://test.api.cheqi.io/v3/api-docs.yaml

fetch-spec:
	curl -fsSL $(SPEC_URL) -o openapi.yaml
	@echo "Spec updated. Run 'make generate' to regenerate models."

generate:
	mvn generate-sources -pl .
	@echo "Models generated in src/main/java/com/cheqi/sdk/models/generated/"

.PHONY: fetch-spec generate
