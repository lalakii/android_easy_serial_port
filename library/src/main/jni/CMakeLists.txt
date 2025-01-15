cmake_minimum_required(VERSION 3.22.1...3.31.1)
project(comm1)
add_library(
        ${PROJECT_NAME}
        SHARED
        SerialPort.c)