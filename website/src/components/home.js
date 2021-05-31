import React, {useState} from 'react';

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';

const Home = () => {
    const [activeMarker, setActiveMarker] = useState(null);
    const [markers, setMarkers] = useState([]);


    return (
    <div>
        <p style={{color: 'green'}}><b>TODO:</b> connect to API and show driving statistics</p>
        <h1>Home</h1>
        <p>Glavna stran za prikaz statistike uporabnika če je ta prijavlen... če ni ga ob zagonu rediratamo na login</p>

        <div id="mapid" className="w-50 m-auto">
            <MapContainer center={[46.55903587583584, 15.63822697025317]} zoom={13} scrollWheelZoom={false}>
                <TileLayer
                    attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {/* <Marker position={[51.505, -0.09]}>
                    <Popup>
                        A pretty CSS3 popup. <br /> Easily customizable.
                    </Popup>
                </Marker> */}

                {markers.map(marker => {
                    <Marker
                        key={marker._id}
                        position={[
                            marker.latitude,
                            marker.longitude
                        ]}
                    onClick={() => {
                        setActiveMarker(marker);
                    }}
                    ></Marker>
                })}

                {activeMarker && (
                    <Popup
                        position={[
                            activeMarker.latitude,
                            activeMarker.longitude
                        ]}
                    >
                        <div>
                            <h1>activeMarker.type</h1>
                        </div>
                    </Popup>
                )}

            </MapContainer>
        </div>
    </div>
    )
}

export default Home;