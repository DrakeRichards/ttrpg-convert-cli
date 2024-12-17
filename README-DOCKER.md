# Docker Instructions

1. Clone this repository.
1. Download the [.jar file](https://github.com/ebullient/ttrpg-convert-cli/blob/main/docs/alternateRun.md#use-java-to-run-the-jar) you want to run and place it in the same directory as the Dockerfile.
1. Ensure you have [Docker installed on your system](https://www.docker.com/), active in your PATH, and running.
1. Edit `Dockerfile` to use the correct .jar file name. Replace `<jar-file-name>.jar` with the name of the .jar file you downloaded.

    ```Dockerfile
    COPY <jar-file-name>.jar app.jar
    ```

1. Edit `docker-compose.yml` to point to the correct `data`, `config`, and `output` directories. Replace `<path-to-data>`, `<path-to-config>`, and `<path-to-output>` with the paths to the directories you want to use. By default, the `data` directory is used for input files, the `config` directory is used for configuration files, and the `output` directory is used for output files.

    ```yaml
    volumes:
        - <path-to-data>:/app/data
        - <path-to-config>:/app/config
        - <path-to-output>:/app/output
    ```

1. Open a shell session and navigate to where you cloned the repository.
1. Build the Docker image:

    ```bash
    docker compose build
    ```

1. Test the Docker image:

    ```bash
    docker compose run ttrpg-convert --help
    ```

1. To use the container, run `docker compose run --rm ttrpg-convert` followed by the arguments you would normally pass to the .jar file.

    ```bash
    docker compose run ttrpg-convert <arguments>
    ```
