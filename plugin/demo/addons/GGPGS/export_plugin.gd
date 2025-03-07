@tool
extends EditorPlugin

# A class member to hold the editor export plugin during its lifecycle.
var export_plugin : AndroidExportPlugin
var _dock : Node

func _enter_tree():
	# Initialization of the plugin goes here.
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)
	_add_docks()


func _exit_tree():
	# Clean-up of the plugin goes here.
	remove_export_plugin(export_plugin)
	_remove_docks()
	export_plugin = null


func _add_docks() -> void:
	var dock_name := &"GGPGS"
	_dock = preload("res://addons/GGPGS/ggpgs_dock.tscn").instantiate()
	add_control_to_bottom_panel(_dock, dock_name)

func _remove_docks() -> void:
	remove_control_from_bottom_panel(_dock)
	_dock.free()

class AndroidExportPlugin extends EditorExportPlugin:
	# TODO: Update to your plugin's name.
	var _plugin_name = "GGPGS"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		if debug:
			return PackedStringArray([_plugin_name + "/bin/debug/" + _plugin_name + "-debug.aar"])
		else:
			return PackedStringArray([_plugin_name + "/bin/release/" + _plugin_name + "-release.aar"])

	# func _get_android_dependencies(platform, debug):
	# 	# TODO: Add remote dependices here.
	# 	if debug:
	# 		return PackedStringArray(["com.google.android.gms:play-services-games-v2:20.1.2"])
	# 	else:
	# 		return PackedStringArray(["com.google.android.gms:play-services-games-v2:20.1.2"])

	func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		if not _supports_platform(platform):
			return PackedStringArray()
		
		return PackedStringArray([
			"com.google.code.gson:gson:2.11.0", 
			"com.google.android.gms:play-services-games-v2:20.1.2"
			])

	func _get_android_manifest_application_element_contents(platform: EditorExportPlatform, debug: bool) -> String:
		if not _supports_platform(platform):
			return ""
		
		return "<meta-data android:name=\"com.google.android.gms.games.APP_ID\" android:value=\"@string/game_services_project_id\"/>"


	func _get_name():
		return _plugin_name
