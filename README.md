# Service Mock Generator

A Java Service for auto-generating mock service structures from OpenDocument Spreadsheet (ODS) files 

## 📋 Overview
Automates creation of mock service folders with complete request/response structure based on spreadsheet. 

## 🛠 Prerequisites
- Java 17+ ([Installation Guide](#java-installation))
- LibreOffice/Excel for ODS file creation
- Basic terminal navigation skills

## 🚀 Quick Start

### 1. Create Service Definition File
Create `ServiceMocked.ods` with this structure:

| ServiceName       | ServiceSchema                          | ServiceResponse                      |
|-------------------|----------------------------------------|--------------------------------------|
| user-service      | {"type":"object","properties":{...}}  | {"id":123,"name":"Test User"}        |
| payment-service   | {"type":"array","items":{...}}        | [{"amount":50,"currency":"USD"}]     |

> **Formatting Tips** 
> - Use valid JSON schema formats
> - Maintain consistent quoting

### 2. Java Environment Setup
```bash
# For Debian/Ubuntu systems
sudo apt update && sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
```
## 🛠️ Step 3: Compile the Java Program

1. Open your terminal.
2. Navigate to the directory where `OdsToMockServiceGenerator.java` is located.
3. Run the following command to compile the Java file:

```bash
javac OdsToMockServiceGenerator.java
```
After successful compilation, a file named OdsToMockServiceGenerator.class will be generated in the same directory.

## ▶️ Step 4: Run the Program
##### In the same terminal (and same directory), run the compiled Java class using:
``` java OdsToMockServiceGenerator ```

##### The program will read the ServiceMocked.ods file and generate the required mock service configurations based on the data provided.
