import shutil
import os

def delete_directory(path):
    # Check if the directory exists
    if os.path.exists(path):
        # Recursively delete the directory and its contents
        shutil.rmtree(path)
        print(f"Directory '{path}' has been deleted.")
    else:
        print("Directory does not exist.")

def relocate_project(maven_group):
    # Define the source directory
    src_dir = 'src/main/java/net/glasslauncher/example/'

    # Construct the destination directory path
    dest_dir = 'src/main/java/' + maven_group.replace('.', '/')

    # Ensure the destination directory exists
    if not os.path.exists(dest_dir):
        os.makedirs(dest_dir)

    # Copy the contents from the source to the destination directory
    if os.path.exists(src_dir):
        for item in os.listdir(src_dir):
            src_path = os.path.join(src_dir, item)
            dest_path = os.path.join(dest_dir, item)
            if os.path.isdir(src_path):
                shutil.copytree(src_path, dest_path)
            else:
                shutil.copy2(src_path, dest_path)

        # Remove the original directory and its contents
        shutil.rmtree(src_dir)
    else:
        print("Source directory does not exist.")

def replace_line(file_path, line_number, line_contents):
    # Read the existing lines from the file
    with open(file_path, 'r') as file:
        lines = file.readlines()

    # Check if the specified line number is within the file's range
    if line_number < 1 or line_number > len(lines):
        raise IndexError("Line number out of range")

    # Replace the specified line
    lines[line_number - 1] = line_contents + '\n'

    # Write the modified lines back to the file
    with open(file_path, 'w') as file:
        file.writelines(lines)

if not os.path.exists("src/main/java/net/glasslauncher/example"):
    print("You've already run this command, would you like to wipe your existing data in src/ ?")
    response = input("(y/n)")
    if (response == 'y'):
        for i in range(100):
            if (not os.path.exists("backup/src_before_delete_" + str(i) + "/")):
                if (not os.path.exists("backup/")):
                    os.makedirs("backup/")
                try:
                    shutil.copytree("src/", "backup/src_before_delete_" + str(i) + "/")
                    break
                except Exception as e:
                    pass
        delete_directory("src/")
        shutil.copytree("backup/src/", "src/")
        replace_line('gradle.properties', 14, '	archives_base_name=fabric-example-mod')
        replace_line('gradle.properties', 13, '	maven_group=com.example')
    else:
        quit()

# Example usage
print("mod id example is `examplemod`")
modid = input("mod id: ")
print("mod description example is `This is an example description! Tell everyone what your mod is about!`")
description = input("mod description: ")
print("author example is `Me!`")
author = input("author: ")
print("mod name example is `Example Mod`")
mod_name = input("mod name: ")
print("maven group example is `com.example`")
maven_group = input("maven group: ")

# backup
if (not os.path.exists("backup/")):
    os.makedirs("backup/")
if (not os.path.exists("backup/src/")):
    shutil.copytree("src/", "backup/src/")

replace_line('gradle.properties', 14, '	archives_base_name=' + modid)
replace_line('gradle.properties', 13, '	maven_group=' + maven_group)
replace_line('src/main/resources/fabric.mod.json', 3, '  "id": "' + modid + '",')
replace_line('src/main/resources/fabric.mod.json', 7, '  "description": "' + description + '",')
replace_line('src/main/resources/fabric.mod.json', 9, '    "' + author + '"')
replace_line('src/main/resources/fabric.mod.json', 17, '  "icon": "assets/' + modid + '/icon.png",')
replace_line('src/main/resources/fabric.mod.json', 32, '    "' + modid + '.mixins.json"')
replace_line('src/main/resources/fabric.mod.json', 6, '  "name": "' + mod_name + '",')
os.rename("src/main/resources/assets/examplemod", "src/main/resources/assets/" + modid)
replace_line('src/main/resources/fabric.mod.json', 17, '  "icon": "assets/' + modid + '/icon.png",')
replace_line('src/main/resources/examplemod.mixins.json', 4, '  "package": "' + maven_group + '.' + modid + '.mixin",')
os.rename("src/main/resources/examplemod.mixins.json", "src/main/resources/" + modid + ".mixins.json")
formatted_name = mod_name.replace(' ', '')
replace_line("src/main/java/net/glasslauncher/example/ExampleMod.java", 1, 'package ' + maven_group + '.' + modid + ';')
replace_line("src/main/java/net/glasslauncher/example/ExampleMod.java", 5, 'public class ' + formatted_name + ' implements ModInitializer {')
new_mod_path = (maven_group + '.' + modid).replace('.','/') + '/' + formatted_name
os.makedirs("src/main/java/" + (maven_group + '.' + modid).replace('.','/'))
os.rename("src/main/java/net/glasslauncher/example/ExampleMod.java", "src/main/java/" + new_mod_path + '.java')
replace_line('src/main/resources/fabric.mod.json', 22, '      "' + new_mod_path.replace('/', '.') + '"')
relocate_project(maven_group + "." + modid)

if (not maven_group.startswith("net")):
    delete_directory("src/main/java/net")
else:
    delete_directory("src/main/java/net/glasslauncher")

