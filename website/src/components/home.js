import React, {useEffect, useState} from 'react';

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';

const Home = () => {
    const [activeMarker, setActiveMarker] = useState(null);
    const [markersRoadSigns, setMarkersRoadSigns] = useState(null);
    // const [markersRoadQuality, setMarkersRoadQuality] = useState([]);
    

    var user = JSON.parse(localStorage.getItem("userSessionID"));

    useEffect(() => {
        //get road signs locations and types -> setMarksers
        async function fetchAPI() {
            //get all user drives
            let response = await fetch('https://rt21-api.herokuapp.com/api/drive/getDrives/' + user._id.$oid, {
                method: 'GET',
                headers: {
                    'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
                }
            });
            if (!response.ok) {
                response.json().then(res => {
                    console.log(res.error);
                });
                return;
            } 

            //store all user drives
            var data = await response.json();
            //get latest user drive
            var latesDrive = data[2]._id.$oid;

            //get all road quality location of latest drive
            response = await fetch('https://rt21-api.herokuapp.com/api/sign/getSigns/' + latesDrive, {
                method: 'GET',
                headers: {
                    'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
                }
            });  
            if (!response.ok) {
                response.json().then(res => {
                    console.log(res.error);
                });
                return;
            } 

            //store all locations drives
            data = await response.json();
            
            let tmpArray = [];
            Object.entries(data).forEach(([key, value]) => {
                tmpArray.push(value);
            });

            await setMarkersRoadSigns(tmpArray);
        }
        
        if (localStorage.getItem("userSessionID") !== null) {
            fetchAPI();
            console.log(markersRoadSigns);
        }
    }, []);

    return (
    <div>
        <p style={{color: 'green'}}><b>TODO:</b> connect to API and show driving statistics</p>
        <p style={{color: 'green'}}><b>TODO:</b> make list of all old drives -{'>'} onClick() ={'>'} get locations and show them on the map</p>
        <p style={{color: 'green'}}><b>TODO:</b> make 1st tab that show road quality and 2nd tab that shows road signs</p>
        
        <div className="text-center mb-3">
            <h1>Home</h1>
        </div>

        {(localStorage.getItem("userSessionID") === null ? 
            <div className="card w-25 m-auto text-center p-3">
                <p>For accessing to driving statistics and road signs detected while driving please<br/>
                <a href="/login">log in</a> or <a href="/register">create a new account</a>
                <br/>and start your new journey.</p>
            </div>   
            
            :

            <div id="mapid" className="w-50 m-auto">
                <MapContainer center={[46.55903587583584, 15.63822697025317]} zoom={13} scrollWheelZoom={false}>
                    <TileLayer
                        attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    <Marker position={[46.55903587583584, 15.63822697025317]}>
                        <Popup>
                            Maribort, FERI
                        </Popup>
                    </Marker>

                    <Marker position={[46.34076659732073, 15.431010914065373]}>
                        <Popup>
                            Konjice
                        </Popup>
                    </Marker>

                    { markersRoadSigns !== null && markersRoadSigns.map(marker => {
                        console.log(marker);
                        <Marker
                            //key={marker._id}
                            position={[
                                marker.latitude,
                                marker.longitude
                            ]}
                        onClick={() => {
                            setActiveMarker(marker);
                        }}
                        >
                            <Popup>
                                marker.type
                            </Popup>
                        </Marker>
                    })}
                    
                    {/* {activeMarker && (
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
                    )} */}

                </MapContainer>
            </div>
        )}
    </div>
    )
}

export default Home;