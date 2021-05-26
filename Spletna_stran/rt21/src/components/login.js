import React from 'react';

const Login = (props) => {
    return (
        <div className="card w-75 m-auto mt-5">
            <div className="card-header text-center">
                <h3>Login</h3>
            </div>
            <div className="card-body">
                <form>
                    <div className="input-group mb-3">
                        <span className="input-group-addon"><i className="glyphicon glyphicon-user"></i></span>
                        <input type="text" className="form-control" name="username" placeholder="Username"/>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                        <input type="password" className="form-control" name="password" placeholder="Password"/>
                    </div>
                    <div className="text-center">
                        <input type="submit" className="btn btn-primary w-50" style={{fontSize: 17}} value="Login"/>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Login;