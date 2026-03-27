ifeq ($(OS),Windows_NT)
GRADLEW := gradlew.bat
else
GRADLEW := ./gradlew
endif

.PHONY: lint

lint:
	$(GRADLEW) detekt
