import React from 'react';
import { Link, Route, Switch, BrowserRouter as Router } from 'react-router-dom';

import Login from './login';
import Register from './register';
import Home from './home';

const Navigation = () => {
    return (
    <div>
        <Router>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <ul className="navbar-nav">
                    <li className="nav-item">
                        {/* <Link to="/">Home</Link> */}
                        <a className="nav-link" href="/">Home</a>
                    </li>
                    <li className="nav-item">
                        {/* <Link to="/login">Login</Link> */}
                        <a className="nav-link" href="/login">Login</a>
                    </li>
                    <li className="nav-item">
                        {/* <Link to="/register">Register</Link> */}
                        <a className="nav-link" href="/register">Register</a>
                    </li>
                </ul>
            </nav>
        
            <Switch>
                <Route path="/" exact>
                <Home/>
                </Route>
                <Route path="/login">
                <Login user={"testing"}/>
                </Route>
                <Route path="/register">
                <Register />
                </Route>
            </Switch>
        </Router>
    </div>
    )
}

export default Navigation;