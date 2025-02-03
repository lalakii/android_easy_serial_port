cmake_minimum_required(VERSION 3.18.1...3.31.4)
project(comm1)
add_library(
        ${PROJECT_NAME}
        SHARED
        SerialPort.c)