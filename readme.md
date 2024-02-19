# GPTGOD Bukkit Plugin

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

- go to `plugins/gptgodmc/config.yml`
- paste in your OpenAi API key
- run the server
- launch minecraft with fabric
- connect to the server at `localhost`
