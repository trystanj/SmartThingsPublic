/*
* Author: Trystan Johnson
*
* original source: https://community.smartthings.com/t/beta-release-uri-switch-device-handler-for-controlling-items-via-http-calls/37842/9
* 
* resources for tv:
*   - http://pastebin.com/wQJPKLbU
*   - https://github.com/samuelmatis/viera-control-v2/blob/master/app/index.html
*
* TODO: Allow Google Home / IFTTT to pass in words - "Off", "Mute", "Change Channel to NBC", etc
* Current IFTTT only seems to be able to trigger a binary switch
*
* Device Handler
*/

metadata {
    definition (name: "Panasonic Viera Power", namespace: "trystanj", author: "Trystan Johnson") {
    	capability "Switch"
        capability "Momentary"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles {
		standardTile("switch", "device.switch", width: 3, height: 3, canChangeIcon: true) {
			state "off", label: 'Turn Off', action: "momentary.push", backgroundColor: "#ff8000", nextState: "on"
			state "on", label: 'Turning Off', action: "momentary.push", backgroundColor: "#ff0000"
		}
		main "switch"
		details "switch"
    }
    
    // get the ip:port from settings (definitions: http://docs.smartthings.com/en/latest/device-type-developers-guide/device-preferences.html)
    preferences {
    	input name: "ipAddress", type: "text", title: "TV IP Address", description: "Local (in-network) IP of the TV", defaultValue: "192.168.", required: true, displayDuringSetup: true
        input name: "port", type: "number", title: "TV port", description: "Port to send commands to", defaultValue: 55000, required: true, displayDuringSetup: true
        
        // "mute" can be useful for testing without having to turn the tv back on
        input name: "command", type: "enum", title: "Command", description: "Command to send", options: ["Power", "Mute"], required: true, displayDuringSetup: true
    }
}

def parse(String description) {
}

def push() {
    def command = "NRC_$command-ONOFF".toUpperCase()
    
    def result = new physicalgraph.device.HubSoapAction(
        path:    "/nrc/control_0",
        urn:     "urn:panasonic-com:service:p00NetworkControl:1",
        action:  "X_SendKey",
        body:    [X_KeyEvent: command],
        headers: [Host: "$ipAddress:$port", CONNECTION: "close"]
    )
        
    sendHubCommand(result)
    
	sendEvent(name: "switch", value: "on", isStateChange: true, displayed: false)
	sendEvent(name: "switch", value: "off", isStateChange: true, displayed: false)
	sendEvent(name: "momentary", value: "pushed", isStateChange: true)
 	
    log.debug("Triggered HTTP request to TV")
}