# SimpleGLTF2

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/MESLewis/SimpleGLTF2/Java%20CI?style=plastic)
![GitHub](https://img.shields.io/github/license/MESLewis/SimpleGLTF2)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/MESLewis/SimpleGLTF2)
![GitHub contributors](https://img.shields.io/github/contributors/MESLewis/SimpleGLTF2)

A WIP deserializer / loader for GLTF2 written in Java.
This project provides a basic deserializer to aid in loading of GLTF2 files.

## Project Goals

SimpleGLTF2 is designed to be a simple library to load GLTF2 files into the Java ecosystem. Part of that ecosystem, and one of the motivations for writing this library, is fetching files from the web, from jar's, and from disk.

## Motivation

I beleive that glTF2 is a great and interesting file format for all uses of 3d models. Assimp failed to load my glTF2 files in my personal project, likely due to loading over the network. This has led me to learn more about glTF2 and a desire to support it directly inside of the Java ecosystem. I am aware of the [JglTF](https://github.com/javagl/JglTF) but I do not like the complexity that brings by supporting both glTF1 and glTF2.

## Repository Layout

The [data](https://github.com/MESLewis/SimpleGLTF2/tree/master/src/main/java/com/meslewis/simplegltf2) package
contains Java classes to hold deserialized GLTF2 JSON.

The [data-only](https://github.com/MESLewis/SimpleGLTF2/tree/data-only/src/main/java/com/meslewis/simplegltf2/data)
branch maintains a bare-bones implementation of `data`, suitable for modification for other projects.

## Getting Started

Standard Gradle Java tasks for building / running.

### Prerequisites

Dependencies are managed by Gradle.

### Installing

TODO

## Contributing

Pull Requests are welcome! As this project is still in its very early stages I may not immediately accept pull requests while still working on design goals and strategies.

## Versioning

WIP

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
