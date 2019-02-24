<?php

namespace App\Http\Controllers;

use App\Models\Entities\User;
use Illuminate\Http\Request;
use App\Models\Services\UserManager;

class UserController extends Controller
{
    public function __construct(UserManager $userManager)
    {
        $this->UserManager = $userManager;
    }
    
        public function postLogin(Request $request)
    {
       $login_id=$request->input('username');
       $password=$request->input('password');
       $response["auth"] = 0;
       $response= $this->UserManager->login($login_id,$password);
        //if you ever need to manipulate its user object
       $user=$response["user"];
       return $response['json'];
    }
    
    public function postRegister(Request $request)
    {
        $data['name']=$request->input('name');
        $data['username']=$request->input('username');
        $data['email']=$request->input('email');
        $data['password']=$request->input('password');
        $response= $this->UserManager->register($data);
        return $response['json'];
     
    }
    
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
        
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        //
    }
    

}
