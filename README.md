# Vibrant F3

A Minecraft Fabric mod that adds vibrant color customization to the F3 debug screen options.

> **Made with ❤️ for the Minecraft community**

## Features

- **Easy To Use**: Integrated directly into the F3 options screen, Quickly customize settings by pressing F3 + F6
- **Compatible**: Compatible with most mods that add things to the F3 menu
- **Client-Side**: Lightweight mod that works entirely on the client side

## Requirements

- **Minecraft Version**: 1.21.11
- **Fabric Loader**: v0.18.4 or higher
- **Fabric API**: Latest compatible version

## Usage

1. Launch Minecraft with the mod installed
2. Press **F3 + F6** to access the F3 options menu
3. Use the **color picker button** next to each option to customize its color
4. Your settings are automatically saved to the configuration file, and applied instantly

## Configuration

The mod stores its configuration in `config/vibrant_f3.json`.

All settings are configurable through the in-game interface, using Mod Menu or by editing the JSON file directly.

## Building from Source

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- Git

### Build Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/amiralimollaei/Vibrant-F3.git
   cd Vibrant-F3
   ```

2. Build the project using Gradle:

   ```bash
   ./gradlew build
   ```

3. The built JAR file will be located in:

   ```text
   build/libs/Vibrant-F3-1.0.0.jar
   ```

## Project Structure

```text
src/
├── client/
│   ├── java/io/github/amitalimollaei/mods/vibrantf3/
│   │   ├── gui/                 # GUI components (color picker button)
│   │   ├── mixin/               # Mixin classes for Minecraft integration
│   │   ├── debug/               # Debug screen display utilities
│   │   ├── storage/             # Configuration management
│   │   └── VibrantF3Client.java # Main entry point
│   └── resources/
│       ├── fabric.mod.json      # Fabric mod metadata
│       ├── vibrant_f3.mixins.json # Mixin configuration
│       └── vibrant_f3.classtweaker # Access widener file
```

## Contributing

Contributions are welcome! If you'd like to contribute to this project:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

If you encounter any issues or have questions:

- Open an issue on the [GitHub repository](https://github.com/amiralimollaei/Vibrant-F3/issues)
- Check existing issues for solutions

## Changelog

### Version 1.0.0

- Initial release
- Color picker integration with F3 options screen
- Automatic configuration saving and loading
- Support for Minecraft 1.21.11

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
