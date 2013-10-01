################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../SOIL/SOIL.c \
../SOIL/image_DXT.c \
../SOIL/image_helper.c \
../SOIL/stb_image_aug.c 

OBJS += \
./SOIL/SOIL.o \
./SOIL/image_DXT.o \
./SOIL/image_helper.o \
./SOIL/stb_image_aug.o 

C_DEPS += \
./SOIL/SOIL.d \
./SOIL/image_DXT.d \
./SOIL/image_helper.d \
./SOIL/stb_image_aug.d 


# Each subdirectory must supply rules for building sources it contributes
SOIL/%.o: ../SOIL/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


