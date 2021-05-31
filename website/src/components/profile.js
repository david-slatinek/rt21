import React from 'react';

const Profile = (props) => {
    return (
        <div className="w-75 m-auto mt-5">
            <h2>Profile</h2>
            <h3>Wellcome {props.user}</h3>
            <p style={{color: 'green'}}><b>TODO: </b>Show user profile data on this page</p>

            <br/>
            <br/>
            <br/>

            <hr/>
            <div className="card w-75 m-auto">
                <p style={{color: 'green'}}><b>TODO:</b> connect to API and change password of current user</p>
                <div className="card-header">
                    <h6>Spremeni geslo</h6>
                </div>
                <div className="card-body">
                    <form>
                        <div className="input-group mb-3">
                            <input type="password" className="form-control" name="password" placeholder="Password"/>
                        </div>
                        <div className="text-center">
                            <input type="submit" className="btn btn-primary w-50" style={{fontSize: 17}} value="Login"/>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default Profile;