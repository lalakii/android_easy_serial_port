cmake_minimum_required(VERSION 3.22.1)
project(comm1)
add_library(
        ${PROJECT_NAME}
        SHARED
        ASerialPort.c
)