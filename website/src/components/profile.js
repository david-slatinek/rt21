import React from 'react';
import { useState } from 'react/cjs/react.development';

const Profile = (props) => {
    const [edit, setEdit] = useState(false);
    const [password, setPassword] = useState(''); 
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    var user = JSON.parse(localStorage.getItem("userSessionID"));

    async function onChangePassword() {
        console.log("change password");
        
        const formData = new FormData();
        formData.append("key", "password");
        formData.append("value", password);

        await fetch('https://rt21-api.herokuapp.com/api/user/' + user._id.$oid, {
            method: 'PUT',
            headers: {
                'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
            },
            body: formData   
        })
        .then(response => {
            if (!response.ok) {
                setError(true);
                setErrorMessage('HTTP status code ' + response.status)
                throw new Error("HTTP status code " + response.status);
            }
            return response.json();
        })
        .then(data => {
            setPassword("");

            console.log(data);

            setEdit(false);
            setSuccess(true);
        })
        .catch((error) => {
            console.log("error: " + error);
            setError(true);
            setErrorMessage('error: ' + error);
        });
    }

    return (
        <div className="w-75 m-auto mt-3">
            <div className="card w-75 m-auto">
                <div className="card-header text-center">
                    <h2>Profile</h2>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col-sm-3">
                            <h6 className="mb-0">Full Name</h6>
                        </div>
                        <div className="col-sm-9 text-secondary">
                            {user.name} {user.last_name}
                        </div>
                    </div>
                    <hr/>
                    <div className="row">
                        <div className="col-sm-3">
                            <h6 className="mb-0">Age</h6>
                        </div>
                        <div className="col-sm-9 text-secondary">
                            {user.age}
                        </div>
                    </div>
                    <hr/>
                    <div className="row">
                        <div className="col-sm-3">
                            <h6 className="mb-0">Username</h6>
                        </div>
                        <div className="col-sm-9 text-secondary">
                            {user.nickname}
                        </div>
                    </div>
                    <hr/>
                    <div className="row">
                        <div className="col-sm-3">
                            <h6 className="mb-0">Email</h6>
                        </div>
                        <div className="col-sm-9 text-secondary">
                            {user.email}
                        </div>
                    </div>
                    <hr/>
                    <div className="row">
                        <div className="col-sm-3">
                            <h6 className="mb-0">Password</h6>
                        </div>
                        <div className="col-sm-9 text-secondary">
                            <div className="row">
                                <div className="col-sm-3">
                                    <button className="btn btn-info" onClick={() => {
                                        if (edit) {
                                            setEdit(false)
                                        } else {
                                            setEdit(true);
                                            setErrorMessage('');
                                            setError(false);
                                            setSuccess(false);
                                        }
                                    }}> 
                                        {(edit ? "Cancle" : "Edit")}
                                    </button>
                                </div>
                                <div className="col-sm-9">
                                    {(success ? <div className="alert alert-success" role="alert">SUCCESS</div> : null)}
                                    {(error ? <div className="alert alert-danger" role="alert">{errorMessage}</div> : null)}
                                </div>
                            </div>
                            
                            { edit && 
                                <div className="card mt-2">
                                    <div className="card-header text-center">
                                        <h3>Change password</h3>
                                    </div>
                                    <div className="card-body">
                                        <form>
                                            <div className="input-group mb-3">
                                                <input type="password" className="form-control" name="password" placeholder="Password" value={password} onChange={(e) => {setPassword(e.target.value)}}/>
                                            </div>
                                            <div className="text-center">
                                                <input type="button" className="btn btn-info" style={{fontSize: 17}} value="Save" onClick={onChangePassword}/>
                                            </div>
                                        </form>
                                    </div>
                                </div> 
                            }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Profile;