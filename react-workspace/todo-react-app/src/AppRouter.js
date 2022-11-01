import { Box, Typography } from '@mui/material'
import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from './App';
import Login from './Login';
import "./index.css";
import SignUp from './SignUp';



function Copyright(){
    return(
        <Typography variant = "body2" color = "textSecondary" align = "center">
            {"Copyright ㉿"}
            mooonoeEngineer, {new Date().getFullYear()}
            {"."}
        </Typography>
    );
};


const AppRouter = () => {
  return (
    <div>
        <BrowserRouter>
            <Routes>
                <Route path = "/" element = {<App/>}/>
                <Route path = "login" element = {<Login/>}/>
                <Route path = "signup" element = {<SignUp/>}/>
            </Routes>
        </BrowserRouter>
        <Box mt={8}>
            <Copyright/>
        </Box>
    </div>
  );
};

export default AppRouter;