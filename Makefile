.PHONY: clean test install release

install:
	LEIN_USE_BOOTCLASSPATH=no lein do cache-version, monolith each install 

deploy:
	LEIN_USE_BOOTCLASSPATH=no lein do cache-version, release $(VERSION)

test:
	LEIN_USE_BOOTCLASSPATH=no lein monolith each :in gorillalabs.jonase/kibit test-all

clean:
	LEIN_USE_BOOTCLASSPATH=no lein monolith each clean