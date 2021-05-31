import React from 'react';
import { useState } from 'react/cjs/react.development';

const Profile = (props) => {
    const [edit, setEdit] = useState(false);
    var user = JSON.parse(localStorage.getItem("userSessionID"));

    function onChangePassword() {
        console.log("change password");
        //TODO - implement
    }

    return (
        <div className="w-75 m-auto mt-3">
            <div className="card w-75 m-auto">
                <div className="card-header text-center">
                    <h2>Profile</h2>
                </div>
                <div className="card-body">
                    <div class="row">
                        <div class="col-sm-3">
                            <h6 class="mb-0">Full Name</h6>
                        </div>
                        <div class="col-sm-9 text-secondary">
                            {user.name} {user.last_name}
                        </div>
                    </div>
                    <hr/>
                    <div class="row">
                        <div class="col-sm-3">
                            <h6 class="mb-0">Age</h6>
                        </div>
                        <div class="col-sm-9 text-secondary">
                            {user.age}
                        </div>
                    </div>
                    <hr/>
                    <div class="row">
                        <div class="col-sm-3">
                            <h6 class="mb-0">Username</h6>
                        </div>
                        <div class="col-sm-9 text-secondary">
                            {user.nickname}
                        </div>
                    </div>
                    <hr/>
                    <div class="row">
                        <div class="col-sm-3">
                            <h6 class="mb-0">Email</h6>
                        </div>
                        <div class="col-sm-9 text-secondary">
                            {user.email}
                        </div>
                    </div>
                    <hr/>
                    <div class="row">
                        <div class="col-sm-3">
                            <h6 class="mb-0">Password</h6>
                        </div>
                        <div class="col-sm-9 text-secondary">
                            <a class="btn btn-info" onClick={() => {
                                if (edit) {
                                    setEdit(false)
                                } else {
                                    setEdit(true);
                                }
                            }}> 
                                {(edit ? "Cancle" : "Edit")}
                            </a>
                            { edit && 
                                <div className="card mt-2">
                                    <div className="card-header text-center">
                                        <p style={{color: 'green', fontSize: 12}}><b>TODO:</b> connect to API and change password of current user</p>
                                        <h3>Change password</h3>
                                    </div>
                                    <div className="card-body">
                                        <form onSubmit={onChangePassword}>
                                            <div className="input-group mb-3">
                                                <input type="password" className="form-control" name="password" placeholder="Password"/>
                                            </div>
                                            <div className="text-center">
                                                <input type="submit" className="btn btn-info" style={{fontSize: 17}} value="Save"/>
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