cmake_minimum_required(VERSION 2.8.3)
project(android_nav)

find_package(catkin REQUIRED)

catkin_package()

execute_process(
  COMMAND ant debug
  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
)

