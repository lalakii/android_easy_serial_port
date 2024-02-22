cmake_minimum_required(VERSION 3.22.1)
project(comm1)
add_library(
        ${PROJECT_NAME}
        SHARED
        SerialPort.c)
find_library(
        log-lib
        log)
target_link_libraries(
        ${PROJECT_NAME}
        ${log-lib})