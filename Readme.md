# Gen AI Spring Boot

A Spring Boot REST API for experimenting with generative AI chat integrations through Spring AI and a direct OpenAI HTTP client.

The application currently supports:

- Summarizing text with a Spring AI `ChatClient`
- Summarizing text with a direct Apache HTTP client call to OpenAI
- Drafting professional emails from user input
- Running local model experiments with Ollama / Docker model endpoints

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Spring AI 2.0.0
- Maven
- Apache HttpClient 5
- OpenAI-compatible chat APIs
- Ollama

## Project Structure

The main Spring Boot application class is `GenAiSbApplication`.

The `config` package contains `AppConfig`, which creates separate Spring AI chat clients for OpenAI and Ollama models.

The `controller` package contains the REST API controllers for summarization and email drafting.

The `client` package contains `OpenAIClientService`, which demonstrates calling the OpenAI chat completions API directly with Apache HttpClient.

The `exception` package contains the custom `OpenAIChatException`.

The `hugging-face` folder contains a helper script for testing Hugging Face summarization, and the `ollama` folder contains a Mistral model manifest reference.

## Configuration

Application settings live in `src/main/resources/application.yaml`.

The application runs on port `8083`.

The Spring AI OpenAI client is configured with an OpenAI-compatible local base URL at `http://localhost:12434/engines/v1` and uses the `docker.io/ai/gemma3:4b` model.

The Ollama configuration points to `http://localhost:11434` and uses the `mistral:7b` model with a temperature of `0.7`.

The direct OpenAI HTTP client reads the API key from the `OPENAI_API_KEY` environment variable and uses the `gpt-4o` model.

## Run Locally

Start the application with the Maven wrapper. Once running, the API is available at `http://localhost:8083`.

## API Endpoints

### Summarize With Spring AI

The `/v1/api/chat/summarize` endpoint accepts plain text in the request body and returns a model-generated summary using the configured Spring AI `openAIChatClient` bean.

### Summarize With Direct OpenAI HTTP Client

The `/v1/api/chat/summarize-with-openai-with-http-client` endpoint accepts plain text in the request body and sends it to OpenAI through `OpenAIClientService`.

This service builds the chat completions request manually with Apache HttpClient and parses the OpenAI response with Jackson.

### Draft Email

The `/v1/api/ollama/chat/draft-email` endpoint accepts plain text in the request body and returns a professional email draft.

It uses a system prompt that asks the model to write clear, polite, concise, and formal emails.

Note: the current controller injects the `openAIChatClient` bean for this endpoint.

## Ollama Setup

If you want to use the configured Ollama model, make sure Ollama is running locally and the `mistral:7b` model is available.

The repository also includes `ollama/mistral-manifests.json` as a model manifest reference.

## Open WebUI

Open WebUI can be used as a browser-based interface for local model experiments.

It can be started with Docker using host networking, persistent backend storage, and an Ollama-compatible base URL.

```bash
docker run -d \
  --network=host \
  -v open-webui:/app/backend/data \
  -e OLLAMA_BASE_URL=http://127.0.0.1:11434/engines/v1 \
  --name open-webui \
  --restart always \
  ghcr.io/open-webui/open-webui:main
```

## Hugging Face Script

The `hugging-face/hugging-face.sh` script calls the Hugging Face Inference Router with `facebook/bart-large-cnn` for summarization.

It requires the `HUGGINGFACE_API_KEY` environment variable before it can call the Hugging Face API.

## Tests

The project includes a basic Spring Boot context-loading test in `GenAiSbApplicationTests`.

Run the Maven test lifecycle to verify that the application context starts successfully.
