/*
* Author: trystanj
*
* source: https://community.smartthings.com/t/beta-release-uri-switch-device-handler-for-controlling-items-via-http-calls/37842/9
* 
* resources for tv:
*   - http://pastebin.com/wQJPKLbU
*   - https://github.com/samuelmatis/viera-control-v2/blob/master/app/index.html
* Device Handler
*/

metadata {
    definition (name: "TV Switch", namespace: "trystan", author: "Trystan Johnson") {
        capability "Actuator"
        capability "Switch"
        capability "Momentary"
        capability "Sensor"
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
}

def parse(String description) {
}

def push() {
	//def command = "NRC_MUTE-ONOFF" // used for testing without having to turn the tv back on
    def command = "NRC_POWER-ONOFF"
  	def result = new physicalgraph.device.HubAction(
        method: "POST",
        path: "/nrc/control_0",
        headers: [
            HOST: "192.168.0.106:55000",
            SOAPAction: "\"urn:panasonic-com:service:p00NetworkControl:1#X_SendKey\"",
            Connection: "close"
        ],
        body: "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:X_SendKey xmlns:u=\"urn:panasonic-com:service:p00NetworkControl:1\"><X_KeyEvent>${command}</X_KeyEvent></u:X_SendKey></s:Body></s:Envelope>"
    )
    
    sendHubCommand(result)
    
	sendEvent(name: "switch", value: "on", isStateChange: true, displayed: false)
	sendEvent(name: "switch", value: "off", isStateChange: true, displayed: false)
	sendEvent(name: "momentary", value: "pushed", isStateChange: true)
}

def off() {
	log.debug("turning off")
	push()
}

def on() {
	log.debug("turning on")
    push()
}