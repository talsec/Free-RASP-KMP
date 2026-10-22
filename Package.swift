// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "FreeRASP_KMP",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "FreeRASP_KMP",
            targets: ["TalsecBridge", "TalsecRuntime"]
        )
    ],
    targets: [
        .binaryTarget(
            name: "TalsecBridge",
            path: "library/src/iosMain/nativeTalsec/TalsecBridge.xcframework"
        ),
        .binaryTarget(
            name: "TalsecRuntime",
            path: "library/src/iosMain/nativeTalsec/TalsecRuntime.xcframework"
        )
    ]
)
