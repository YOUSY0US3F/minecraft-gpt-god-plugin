# ![GPTGodIcon](https://github.com/user-attachments/assets/15ee2068-82d8-419a-9247-17332ec84600) GPTGOD Bukkit Plugin

## Local Setup

- clone the repo
- run `./gradlew` in the root of the repo
- download [paper mc server version 1.20.4](https://papermc.io/downloads/paper)
- setup the server
- download [voicechat bukkit plugin 2.5.1](https://modrinth.com/plugin/simple-voice-chat/version/bukkit-2.5.1)
- place the jar in the plugins folder of your server
- [install fabric for 1.20.4](https://fabricmc.net/use/installer/)
- download [the voice chat mod version 1.20.4-2.5.4 for fabric](https://modrinth.com/plugin/simple-voice-chat/version/fabric-1.20.4-2.5.4)
- place that in your mods folder in .minecraft

## Building

- use the shadowjar task to build
- the jar will appear in build/libs
- place this jar in the plugins folder of the server

## Dependencies

to add a dependency add it like this:

``` Groovy

dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
}

shadowJar {
    dependencies {
        include(dependency('com.google.code.gson:.*'))
    }
}

```

## Running

- start the server once to generate `plugins/gptgodmc/config.yml`
- set `inference-provider` in `config.yml` to one of:
    - `openai`
    - `openrouter`
    - `ollama`
    - `lmstudio`
    - `generic`
    - `nim`

### Provider Configuration

Use only the values needed for your selected provider:

URL behavior from this PR:

- For `openrouter`, `lmstudio`, `generic`, and `nim`, you can provide either:
    - a base URL (for example `https://api.openrouter.ai`), or
    - a `/v1` URL (for example `https://api.openrouter.ai/v1`), or
    - a full chat endpoint ending in `/chat/completions`
- The plugin normalizes these automatically for OpenAI-compatible providers:
    - if URL ends with `/chat/completions`, it is used as-is
    - if URL ends with `/v1`, `/chat/completions` is appended
    - otherwise `/v1/chat/completions` is appended
- Trailing `/` is stripped automatically.
- For `ollama`, set only the server base URL (for example `http://localhost:11434`); the plugin always appends `/api/generate`.

- `openai`
    - set `openAiKey`
- `openrouter`
    - set `openRouterKey`
    - optional: set `openRouterUrl` (default: `https://api.openrouter.ai/v1`)
- `ollama`
    - set `ollamaUrl` if needed (default: `http://localhost:11434`)
    - note: tool/function calling is not currently supported when using Ollama in this plugin
- `lmstudio`
    - set `lmstudioUrl` if needed (default: `http://localhost:1234`)
    - uses LM Studio's OpenAI-compatible chat endpoint
- `generic`
    - set `genericUrl` to your OpenAI-compatible provider URL (base, `/v1`, or full `/chat/completions` endpoint)
    - set `genericKey`
- `nim`
    - set `nimUrl` if needed (default: `https://integrate.api.nvidia.com/v1`)
    - set `nimKey`
    - optional: set `nimExtraBody` with JSON for NIM-specific request params

### Example Configs

OpenAI:

```yaml
inference-provider: openai
openAiKey: "YOUR_OPENAI_KEY"
```

OpenRouter:

```yaml
inference-provider: openrouter
openRouterKey: "YOUR_OPENROUTER_KEY"
openRouterUrl: "https://api.openrouter.ai/v1"
```

Ollama (local):

```yaml
inference-provider: ollama
ollamaUrl: "http://localhost:11434"
```

LM Studio (local):

```yaml
inference-provider: lmstudio
lmstudioUrl: "http://localhost:1234"
```

Generic OpenAI-compatible provider:

```yaml
inference-provider: generic
genericUrl: "https://api.your-provider.example"
genericKey: "YOUR_PROVIDER_KEY"
```

NVIDIA NIM:

```yaml
inference-provider: nim
nimUrl: "https://integrate.api.nvidia.com/v1"
nimKey: "YOUR_NIM_KEY"
nimExtraBody: '{"chat_template_kwargs": {"enable_thinking": true, "clear_thinking": false}}'
```

### Start the plugin

- run the server
- launch Minecraft with Fabric + Simple Voice Chat mod installed
- connect to the server (for local setup, usually `localhost`)
