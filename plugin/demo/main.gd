extends Node2D

@onready var title : Label = %Title
@onready var testlabel : Label = %TestLabel
@onready var playerinfo : Label = %PlayerInfo

var _plugin_name = "GGPGS"
var ggpgs_plugin

func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		ggpgs_plugin = Engine.get_singleton(_plugin_name)
		ggpgs_plugin.connect("userDetailsSignal",_on_userDetailsSignal)
		
	else:
		printerr("ggpgs plugin not found")
		
func _on_userDetailsSignal(data):
	playerinfo.text = str(data)

func _on_login_button_pressed() -> void:
	if ggpgs_plugin:
		ggpgs_plugin.login()
