<?php

namespace App\Http\Controllers;

use App\Models\Entities\Record;
use Illuminate\Http\Request;
use App\Models\Services\RecordManager;
use Carbon\Carbon;
use App\Models\Services\UserManager;
use Intervention\Image\ImageManager;

class RecordController extends Controller {

    public function __construct(RecordManager $recordManager) {
        $this->RecordManager = $recordManager;
    }

//    public function postCreateRecord(Request $request){
//        $data['username']=$request->input('username');
//        $data['password']=$request->input('password');
//        $data['category_id']=$request->input('category_id');
//        $data['record_title']=$request->input('record_title');
//        $data['record_description']=$request->input('record_description');
//        $response = $this->RecordManager->createRecord($data);
//        return $response['json'];
//        
//    }

    public function postCreateRecordTest(Request $request) {
        $data['username'] = $request->input('username');
        $data['password'] = $request->input('password');
        $data['category_id'] = $request->input('category_id');
        $data['record_title'] = $request->input('record_title');
        $data['record_description'] = $request->input('record_description');

        if ($request->has('photo_base64')) {
            $user_id = $request->input('user_id');
            $photo = base64_decode($request->input('photo_base64'));

            $file_name = $user_id . '_' . time() . ".jpg";
            $path = public_path() . '/image/user_photos/' . $file_name;
            file_put_contents($path, $photo);
            $data['record_image'] = $file_name;
        }
             $file = $request->file("uploaded_file");

        if ($request->hasFile("uploaded_file")) {
             $file = $request->file("uploaded_file");
            $user_id = $request->input('user_id');
            $file_name = $user_id . '_' . time() .".3gp";
            $path = public_path() . '/audio/user_audio/';
            $file->move($path, $file_name);
            $data['record_audio'] = $file_name;
        }  

        $response = $this->RecordManager->createRecord($data);
        return $response['json'];
    }

    public function postAllRecords(Request $request) {
        $data['username'] = $request->input('username');
        $data['password'] = $request->input('password');
        $data['category_id'] = $request->input('category_id');
        $response = $this->RecordManager->retrieveRecords($data);
        return $response['json'];
    }
    
    public function postSearchAllRecords(Request $request) {
        $data['username'] = $request->input('username');
        $data['password'] = $request->input('password');
        $data['user_id'] = $request->input('user_id');
        $response = $this->RecordManager->retrieveAllRecords($data);
        return $response['json'];
    }
    
    public function postDeleteRecord(Request $request) {
        $data['username'] = $request->input('username');
        $data['password'] = $request->input('password');
        $data['category_id'] = $request->input('category_id');
        $data['record_id'] = $request->input('record_id');
        $response = $this->RecordManager->deleteRecord($data);
        return $response['json'];
    }

    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index() {
        //
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create() {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request) {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id) {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id) {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id) {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id) {
        //
    }

}
