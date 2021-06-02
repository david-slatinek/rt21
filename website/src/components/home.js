import React, {useEffect, useState} from 'react';

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';

const Home = () => {
    const [roadsigns, setRoadsigns] = useState(true);
    const [drives, setDrives] = useState(null);
    const [markersRoadSigns, setMarkersRoadSigns] = useState(null);
    const [markersRoadQuality, setMarkersRoadQuality] = useState(null);

    var user = JSON.parse(localStorage.getItem("userSessionID"));

    async function getMarkers(id) {
        //get all road quality location
        let response = await fetch('https://rt21-api.herokuapp.com/api/sign/getSigns/' + id, {
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
        let data = await response.json();
        let tmpArray = [];
        Object.entries(data).forEach(([key, value]) => {
            tmpArray.push(value);
        });
        await setMarkersRoadSigns(tmpArray);

        //get all roadsigns location
        response = await fetch('https://rt21-api.herokuapp.com/api/location/getLocations/' + id, {
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
        data = await response.json();
        tmpArray = [];
        Object.entries(data).forEach(([key, value]) => {
            tmpArray.push(value);
        });
        await setMarkersRoadQuality(tmpArray)
    }

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
            let tmpArray = [];
            Object.entries(data).forEach(([key, value]) => {
                tmpArray.push(value);
            });
            //get latest user drive
            setDrives(tmpArray);

            if (Object.entries(tmpArray).length > 0) {
                var latesDrive = data[Object.entries(tmpArray).length - 1]._id.$oid;

                getMarkers(latesDrive);
            } else {

            }
        }
        
        if (localStorage.getItem("userSessionID") !== null) {
            fetchAPI();
        }
    }, []);

    return (
    <div>
        <p style={{color: 'green'}}><b>TODO:</b> make 1st tab that show road quality and 2nd tab that shows road signs</p>

        {(localStorage.getItem("userSessionID") === null ? 
            <div className="card w-25 m-auto text-center p-3">
                <p>For accessing to driving statistics and road signs detected while driving please<br/>
                <a href="/login">log in</a> or <a href="/register">create a new account</a>
                <br/>and start your new journey.</p>
            </div>   
            
            :
            <div className="row">
                <div className="col-md-4 p-2">
                    {
                        drives !== null &&
                        <div className="list-group text-center">
                            { drives.length > 0 && 
                                <div><h4 className="mb-0 mt-3">Old drives locations</h4><hr/></div>
                            }
                            {
                                drives.map(drive => (
                                    <button
                                        key={drive._id.$oid} 
                                        type="button" 
                                        className="list-group-item list-group-item-action mb-2" 
                                        onClick={() => {
                                            getMarkers(drive._id.$oid)
                                        }}
                                        >
                                            {new Date(drive.start * 1000).toLocaleString()} - {new Date(drive.end * 1000).toLocaleString()}
                                        </button>
                                ))
                            }
                        </div>
                    }
                </div>
                <div id="mapid" className="col-md-8">
                    {
                        (markersRoadSigns !== null && markersRoadQuality !== null ?
                        <div id="mapDiv" className="text-center">
                            <button type="button" className="btn btn-dark w-50 mb-3 mt-3" onClick={() => {setRoadsigns(!roadsigns)}}>{!roadsigns ? "Show roadsigns passed" : "Show road quality detected"}</button>
                            <MapContainer center={[markersRoadSigns[0].latitude, markersRoadSigns[0].longitude]} zoom={10} scrollWheelZoom={false}>
                                <TileLayer
                                    attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                />

                                {( roadsigns === true ?
                                    markersRoadSigns.map(marker => (
                                        <Marker
                                            key={marker._id.$oid}
                                            position={[marker.latitude, marker.longitude]}
                                        >
                                            <Popup>
                                                <h4>Roadsign: {marker.type}</h4>
                                            </Popup>
                                        </Marker>
                                    ))

                                    :

                                    markersRoadQuality.map(marker => (
                                        <Marker
                                            key={marker._id.$oid}
                                            position={[marker.latitude, marker.longitude]}
                                        >
                                            <Popup>
                                                <h4>Road quality: {marker.road_quality}</h4>
                                            </Popup>
                                        </Marker>
                                    ))
                                )}
                            </MapContainer>
                        </div>

                        :
                        
                        <div class="alert alert-info mt-5" role="alert"><h5>No drives were made with this account yet.</h5></div>
                    )}
                </div>
            </div>
        )}
    </div>
    )
}

export default Home;